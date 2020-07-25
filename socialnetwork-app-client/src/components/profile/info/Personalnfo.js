import React, { useState, useEffect } from "react";

import "./PersonalInfo.css";

import "bootstrap/dist/css/bootstrap.min.css";

import AuthService from "../../../service/auth/AuthService";

import AppConstants from "../../../constants/AppConstants";

const PersonalInfo = () => {

    const [profile, setProfile] = useState({})
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false) 

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = () => {

        let username = AuthService.getProfile().username;

        const urlGetProfileDetails = AppConstants.API_HOST + "/api/v1/profiles/" + username;
        const token = AuthService.getToken();

        fetch(urlGetProfileDetails, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.json()

        }).then(data => {
            setProfile(data)
            setIsLoaded(true)
        }).catch(() => {
            serError(true)
        });
    };

    if (error) {
        return <div>Error. Message: {error.message}</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {
        return (

            <div>

                <div className="personal-info">
                     <div className="full-name">
                        <h4>{profile.customUser.firstName} {profile.customUser.lastName}</h4>
                    </div>
                    <div className="additional-info">
                        <div className="biirthday">
                            <h5>Birthday: {profile.customUser.customUserDetails.birthday}</h5>
                        </div>
                        <div className="country">
                            <h5>Country: {profile.customUser.customUserDetails.country}</h5>
                        </div>
                        <div className="city">
                            <h5>City: {profile.customUser.customUserDetails.city}</h5>
                        </div>
                        <div className="family-status">
                            <h5>Family status: {profile.customUser.customUserDetails.familyStatus}</h5>
                        </div>
                        <div className="phone">
                            <h5>Phone: {profile.customUser.customUserDetails.phone}</h5>
                        </div>
                        <div className="address">
                            <h5>Address: {profile.customUser.customUserDetails.address}</h5>
                         </div>
                    </div>
                </div>
               
            </div>

        );
    }
}
 
export default PersonalInfo;