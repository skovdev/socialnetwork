import React from "react";

import AppConstants from "../../../../constants/AppConstants";
import HttpMethodConstants from "../../../../constants/HttpMethodConstants";

import AuthService from "../../../../service/auth/AuthService";

const UpdateCurrentAvatar = (props) => {

    const handleOnChangeEvent = (event) => {
        
        const updateAvatarEndpoint = AppConstants.API_HOST + "/profile-service/profiles/avatar?username=" + AuthService.getProfile().username;

        let reader = new FileReader();
        let file = event.target.files[0];

        reader.onloadend = () => {
            props.setAvatar(reader.result.split(',')[1]);
        }

        reader.readAsDataURL(file);

        let data = new FormData();

        data.append("profileAvatar", file);

        fetch(updateAvatarEndpoint, {
            method: HttpMethodConstants.POST,
            headers: {
                "Authorization": "Bearer " + AuthService.getToken()
            },
            body: data
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed upload of avatar");
            }

            return response.json();
        
        }).then(response => {
            console.log(response.message);
        }).catch(error => {
            console.log(error);
        });
    }

    return (
        
        <div className="button-upload-avatar mt-3 mr-3">
            <button className="btn btn-dark">Upload avatar</button>
            <input type="file" onChange={handleOnChangeEvent} name="profileAvatar" />
        </div>

    )
}

export default UpdateCurrentAvatar;