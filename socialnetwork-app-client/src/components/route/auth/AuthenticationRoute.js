import React from "react";

import { Navigate, Route } from "react-router-dom";

import AuthService from "../../../service/auth/AuthService";

const AuthenticationRoute = ({ children, ...rest }) => {
    
    return (
        <Route
            {...rest}
            element={AuthService.getToken() ? children : <Navigate to="/" replace />}
        />
    );
};

export default AuthenticationRoute;
