import React, { useState, useEffect } from "react";

import "./Avatar.css";

import EditButton from "../edit/button/EditButton";

import AuthService from "../../../service/auth/AuthService";

import AppConstants from "../../../constants/AppConstants";

const Avatar = (props) => {

    const [avatar, setAvatar] = useState({})
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false)

    useEffect(() => {
        loadAvatarProfile();
    }, []);

    const loadAvatarProfile = () => {

        const urlGetAvatar = AppConstants.API_HOST + "/api/v1/profile/avatar";
        const token = AuthService.getToken();

        fetch(urlGetAvatar, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.text()

        }).then(data => {
            setAvatar(data)
            setIsLoaded(true)
        }).catch(error => {
            serError(true)
            console.log(error);
        })
    }

    if (error) {
        return <div>Error</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {

        return (

            <div className="avatar-profile">
                <img src={'data:image/jpeg;base64,' + avatar} />
                <EditButton username={props.username} />
            </div>

        )
    }
}

export default Avatar;