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

        const getProfilesByUserId = AppConstants.API_HOST + "/api/v1/profiles/user?userId=" + userId;

        fetch(getProfilesByUserId, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken(),
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw response;
            }
            
            return response.json();

        }).then(data => {
            setProfile(data);
            setIsLoaded(true);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error);
                console.log("Status: " + body.status + " - Error: " + body.error + " - Message: " + body.message);
            });
        })

    }, []);

    if (error) {
        
        return (
            <div className="group-profile">
                <p>Error. Message: {errorMessage}</p>
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