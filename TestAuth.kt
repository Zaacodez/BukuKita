import io.github.jan.supabase.gotrue.auth; import io.github.jan.supabase.SupabaseClient; suspend fun f(supabase: SupabaseClient) { supabase.auth.updateUser() }
