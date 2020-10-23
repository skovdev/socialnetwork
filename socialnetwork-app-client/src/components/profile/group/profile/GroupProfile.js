import React, { useEffect, useState } from "react";

import "./GroupProfile.css";

import AppConstants from "../../../../constants/AppConstants";

import AuthService from "../../../../service/auth/AuthService";

const GroupProfile = (props) => {

    const [profile, setProfile] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState(''); 

    useEffect(() => {

        const userId = props.userId;

        const getProfilesByUserId = AppConstants.API_HOST + "/profile-service/profiles/user?userId=" + userId;

        fetch(getProfilesByUserId, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken(),
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed load profile of user");
            }
            
            return response.json();

        }).then(data => {
            setProfile(data);
            setIsLoaded(true);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            setErrorMessage(error.message);
        })
    }, []);

    if (error) {
        
        return (
            <div className="group-profile">
                <p>{errorMessage}</p>
            </div>
        )
    } else if (!isLoaded) {

        return (
            <div className="group-profile">
                <p>Loading...</p>
            </div>
        )
    } else {
        
        return (
            
            <div className="group-profile">
                <p key={profile.id}>{profile.customUser.firstName}</p>
                <img src={'data:image/jpeg;base64,' + profile.avatar} />   
            </div>
        )
    }
}

export default GroupProfile;