import JwtTokenDecoder from "../../util/jwt/JwtTokenDecoder";

class AuthService {

    static getAuthData() {
        const token = localStorage.getItem("sn_id_token");
        return JwtTokenDecoder.decode(token);
    }

    static setToken(token) {
        localStorage.setItem("sn_id_token", token);
    }

    static getToken() {
        return localStorage.getItem("sn_id_token");
    }

    static logout() {
        localStorage.removeItem("sn_id_token");
    }
}

export default AuthService;