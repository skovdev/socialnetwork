import DecodeJwtToken from "../../util/jwt/DecodeJwtToken";

class AuthService {

    static getProfile() {

        const token = localStorage.sn_id_token;

        return DecodeJwtToken.decode(token);

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