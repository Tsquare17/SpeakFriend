package auth

class Auth(var user: String, var pass: String) {

    fun check(): Boolean {
        return true;
    }
}
