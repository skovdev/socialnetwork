import React, {useEffect, useState} from "react"

import "./AdminHeader.css"

import AuthService from "../../../service/auth/AuthService"

import DecodeJwtToken from "../../../util/jwt/DecodeJwtToken"

import AppConstants from "../../../constants/AppConstants"

const AdminHeader = () => {

    const [profile, setProfile] = useState({})
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false)

    useEffect(() => {
        loadUserProfile();
    }, [])

    const loadUserProfile = () => {

        const token = AuthService.getToken();

        const decodedToken = DecodeJwtToken.decode(token);

        const urlLoadUserProfile = AppConstants.API_HOST + "/api/v1/profiles/" + decodedToken.username;

        fetch(urlLoadUserProfile, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.json()

        }).then(data => {
            setProfile(data)
            setIsLoaded(true)
        }).catch(error => {
            serError(true)
            console.error(error);
        })
    }

    if (error) {
        return <div>Error</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {
        
        return (
            <div className="admin-header">
                <p className="title">SocialNetwork Administration</p>
                <div className="admin-profile">
                    <img src={'data:image/jpeg;base64,' + profile.avatar} className="rounded" />
                    <div className="dropdown-profile">
                        <p className="username">{profile.customUser.username}</p>
                        <div className="dropdown-profile-content">
                            <a href={"/profile/" + profile.customUser.username}>Profile</a>
                            <a href="#">Edit</a>
                            <a href="#">Logout</a>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default AdminHeader;