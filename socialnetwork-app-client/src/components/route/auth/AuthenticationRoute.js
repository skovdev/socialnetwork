import React from "react";

import { Navigate } from "react-router-dom";

import AuthService from "../../../service/auth/AuthService";

const AuthenticationRoute = ({ component }) => {
    return AuthService.getToken() ? component : <Navigate to="/" />;
};

export default AuthenticationRoute;
