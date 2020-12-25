import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";

import "./Main.css";

import Header from "../header/Header";
import Welcome from "../welcome/Welcome";
import Login from "../login/Login";

import AuthService from "../../service/auth/AuthService";

const Main = () => {

    const history = useHistory()
    
    useEffect(() => {

        if (AuthService.getToken()) {
            history.push("/profile/" + AuthService.getProfile().username)
        }
    })

    return (
        <div className="main">
            <Header/>
            <div className="container">
                <Welcome />
                <Login/>
            </div>
        </div>
    );
}
 
export default Main;