package com.example.bukukita.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import com.example.bukukita.data.model.UserProfile
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import android.util.Log

class AuthRepository(private val supabase: SupabaseClient) {

    suspend fun signUp(
        email: String, 
        password: String, 
        username: String? = null, 
        fullName: String? = null,
        avatarBytes: ByteArray? = null
    ) {
        withContext(Dispatchers.IO) {
            val user = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                if (username != null || fullName != null) {
                    val metaData = mutableMapOf<String, kotlinx.serialization.json.JsonElement>()
                    username?.let { metaData["username"] = JsonPrimitive(it) }
                    fullName?.let { metaData["full_name"] = JsonPrimitive(it) }
                    this.data = JsonObject(metaData)
                }
            }

            // Dapatkan user ID setelah sign up (baik dari AuthResponse maupun sesi saat ini)
            val userId = user?.id ?: supabase.auth.currentUserOrNull()?.id
            
            // Upload foto avatar jika ada
            var uploadedAvatarUrl: String? = null
            if (userId != null && avatarBytes != null) {
                try {
                    val fileName = "$userId.jpg"
                    supabase.storage.from("avatars").upload(fileName, avatarBytes, upsert = true)
                    uploadedAvatarUrl = supabase.storage.from("avatars").publicUrl(fileName)
                    Log.d("AuthRepository", "Avatar uploaded successfully: $uploadedAvatarUrl")
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Failed to upload avatar to Supabase", e)
                }
            }
            
            // Update profile (sudah dibuat secara otomatis oleh trigger database)
            if (userId != null) {
                val profileData = buildJsonObject {
                    if (username != null) put("username", username)
                    if (fullName != null) put("full_name", fullName)
                    if (uploadedAvatarUrl != null) put("avatar_url", uploadedAvatarUrl)
                }
                
                if (profileData.isNotEmpty()) {
                    supabase.postgrest["profiles"].update(profileData) {
                        filter {
                            eq("id", userId)
                        }
                    }
                }
            }
        }
    }

    suspend fun signIn(emailOrUsername: String, password: String) {
        withContext(Dispatchers.IO) {
            val input = emailOrUsername.lowercase().trim()
            val loginEmail = if (input.contains("@")) {
                input
            } else {
                val result = supabase.postgrest["profiles"]
                    .select {
                        filter {
                            eq("username", input)
                        }
                    }.decodeSingleOrNull<JsonObject>()
                    
                if (result == null) {
                    throw Exception("Username not found")
                }
                
                val emailFromJson = result["email"]?.jsonPrimitive?.contentOrNull
                
                emailFromJson ?: throw Exception("Email not found for this username")
            }

            supabase.auth.signInWith(Email) {
                this.email = loginEmail
                this.password = password
            }
        }
    }

    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            supabase.auth.signOut()
        }
    }

    fun getCurrentUser(): UserInfo? {
        return supabase.auth.currentUserOrNull()
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeSingleOrNull<JsonObject>()

                if (result != null) {
                    UserProfile(
                        id = result["id"]?.jsonPrimitive?.contentOrNull ?: userId,
                        username = result["username"]?.jsonPrimitive?.contentOrNull ?: "User",
                        fullName = result["full_name"]?.jsonPrimitive?.contentOrNull ?: "Unknown",
                        avatarUrl = result["avatar_url"]?.jsonPrimitive?.contentOrNull
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun updateUserProfile(userId: String, username: String, fullName: String, avatarBytes: ByteArray?, newEmail: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = supabase.auth.currentUserOrNull()?.email
                val emailChanged = newEmail != null && newEmail != currentEmail

                if (emailChanged) {
                    try {
                        Log.d("AuthRepository", "Attempting to update email to: $newEmail")
                        // 1. Update di auth.users terlebih dahulu
                        val updatedUser = supabase.auth.updateUser {
                            email = newEmail
                        }
                        Log.d("AuthRepository", "Email successfully updated in Auth: ${updatedUser?.email}")
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to update email in Supabase Auth", e)
                        throw Exception("Gagal mengupdate email: ${e.message}", e)
                    }
                }

                var uploadedAvatarUrl: String? = null
                if (avatarBytes != null) {
                    try {
                        val fileName = "$userId.jpg"
                        supabase.storage.from("avatars").upload(fileName, avatarBytes, upsert = true)
                        uploadedAvatarUrl = supabase.storage.from("avatars").publicUrl(fileName)
                        Log.d("AuthRepository", "Avatar updated successfully: $uploadedAvatarUrl")
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to upload avatar", e)
                    }
                }

                val profileData = buildJsonObject {
                    put("username", username)
                    put("full_name", fullName)
                    if (uploadedAvatarUrl != null) {
                        put("avatar_url", uploadedAvatarUrl)
                    }
                }

                supabase.postgrest["profiles"].update(profileData) {
                    filter {
                        eq("id", userId)
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to update user profile", e)
                throw e
            }
        }
    }
}
