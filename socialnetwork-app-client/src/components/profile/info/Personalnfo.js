import React, { useState, useEffect } from "react";

import "./PersonalInfo.css";

import "bootstrap/dist/css/bootstrap.min.css";

import AuthService from "../../../service/auth/AuthService";

import AppConstants from "../../../constants/AppConstants";

const PersonalInfo = () => {

    const [profileInfo, setProfileInfo] = useState({})
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false)
    const [errorMessage, setErrorMessage] = useState("") 

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = () => {

        let username = AuthService.getProfile().username;

        const urlGetProfileDetails = AppConstants.API_HOST + "/profiles/user/" + username;
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
            setProfileInfo(data)
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
                        <h4>{profileInfo.firstName} {profileInfo.lastName}</h4>
                    </div>
                    <div className="additional-info">
                        <div className="biirthday">
                            <h5>Birthday: {profileInfo.birthDay}</h5>
                        </div>
                        <div className="country">
                            <h5>Country: {profileInfo.country}</h5>
                        </div>
                        <div className="city">
                            <h5>City: {profileInfo.city}</h5>
                        </div>
                        <div className="family-status">
                            <h5>Family status: {profileInfo.familyStatus}</h5>
                        </div>
                        <div className="phone">
                            <h5>Phone: {profileInfo.phone}</h5>
                        </div>
                        <div className="address">
                            <h5>Address: {profileInfo.address}</h5>
                         </div>
                    </div>
                </div>
               
            </div>

        );
    }
}
 
export default PersonalInfo;