import React, { useState, useEffect } from "react";

import "./PersonalInfo.css";

import "bootstrap/dist/css/bootstrap.min.css";

import AuthService from "../../../service/auth/AuthService";

import AppConstants from "../../../constants/AppConstants";

const PersonalInfo = () => {

    const [profile, setProfile] = useState({})
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false)
    const [errorMessage, setErrorMessage] = useState("") 

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = () => {

        let username = AuthService.getProfile().username;

        const urlGetProfileDetails = AppConstants.API_HOST + "/profile-service/profiles/user/" + username;
        const token = AuthService.getToken();

        fetch(urlGetProfileDetails, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed load profile of user")
            }

            return response.json()

        }).then(data => {
            setProfile(data)
            setIsLoaded(true)
        }).catch(error => {
            serError(true)
            setErrorMessage(error.message)
        });
    };

    if (error) {
        return <div>{errorMessage}</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {
        return (

            <div>

                <div className="personal-info">
                     <div className="full-name">
                        <h4>{profile.user.firstName} {profile.user.lastName}</h4>
                    </div>
                    <div className="additional-info">
                        <div className="biirthday">
                            <h5>Birthday: {profile.user.userDetails.birthday}</h5>
                        </div>
                        <div className="country">
                            <h5>Country: {profile.user.userDetails.country}</h5>
                        </div>
                        <div className="city">
                            <h5>City: {profile.user.userDetails.city}</h5>
                        </div>
                        <div className="family-status">
                            <h5>Family status: {profile.user.userDetails.familyStatus}</h5>
                        </div>
                        <div className="phone">
                            <h5>Phone: {profile.user.userDetails.phone}</h5>
                        </div>
                        <div className="address">
                            <h5>Address: {profile.user.userDetails.address}</h5>
                         </div>
                    </div>
                </div>
               
            </div>

        );
    }
}
 
export default PersonalInfo;