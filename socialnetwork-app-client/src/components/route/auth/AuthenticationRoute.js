import React from "react";

import { Route, Redirect } from "react-router-dom";

import AuthService from "../../../service/auth/AuthService";

const AuthenticationRoute = (props) => {

    const {component: Component, ...rest} = props;

    return (
    
        <Route {...rest} render={(props) => {
                return AuthService.getToken() ? (<Component {...props} />) : (<Redirect to={"/"}/>)
             }}
        />
    )
}

export default AuthenticationRoute;