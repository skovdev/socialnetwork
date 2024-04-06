class JwtTokenDecoder {

    static decode(token) {
        let base64Url = token.split('.')[1];
        return JSON.parse(window.atob(base64Url));
    }
}

export default JwtTokenDecoder;