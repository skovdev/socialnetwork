import React from "react";

import AppConstants from "../../../../constants/AppConstants";

import AuthService from "../../../../service/auth/AuthService";

const DefaultAvatar = (props) => {

    const setDefaultAvatar = () => {

        const setDefaultAvatarEndpoint = AppConstants.API_HOST + "/api/v1/profiles/avatar?username=" + AuthService.getProfile().username;

        fetch(setDefaultAvatarEndpoint, {
            method: "DELETE",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed set default avatar");
            }

            return response.text();

        }).then(avatar => {
            props.setAvatar(avatar);
        }).catch(error => {
            console.log(error);
        });
    }

    return (
        <div className="mt-3">
            <button className="btn btn-dark" onClick={setDefaultAvatar}>Set Default Avatar</button>
        </div>
    )
}

export default DefaultAvatar;