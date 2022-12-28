import React, { useState } from "react";
import { Link, useHistory } from "react-router-dom";

import "./Login.css";

import AuthService from '../../service/auth/AuthService';

import AppConstants from "../../constants/AppConstants";

import DecodeJwtToken from "../../util/jwt/DecodeJwtToken";

const Login = () => {

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const [error, setError] = useState(false)

    const history = useHistory()
    
    const handleSubmit = event => {

        event.preventDefault();

        const loginUrl = AppConstants.API_HOST + "/auth/signin";

        let data = {
            username: username,
            password: password
        };

        fetch(loginUrl,{
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.json()    

        }).then(data => {

            AuthService.setToken(data.token)

            var decodedToken = DecodeJwtToken.decode(data.token);

            if (decodedToken.isAdmin) {
                history.push("/admin/dashboard")
            } else {
                history.push("/profile/" + decodedToken.username);
            }

            setError(false)

        }).catch(() => {
            setError(true)
        })
    }

     if (error) {
        return (  
            <div className="login">
                <p className="error alert alert-danger">Login or password entered incorrectly</p>
            </div>
        )
    } else {
        return (
            <div className="login">
                <form>
                    <input type="text" name="username" id="username" placeholder="Name" value={username} onChange={e => setUsername(e.target.value)} />
                    <input type="password" name="password" id="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                    <input type="submit" className="submit btn btn-dark" onClick={handleSubmit} disabled={!username || !password} value="Sign up" />
                </form>
            </div>
        );
    }
}

 export default Login;