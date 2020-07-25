import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import "./Profile.css";

import Header from "../header/Header";
import Menu from "./menu/Menu";
import PersonalInfo from "./info/Personalnfo";
import Avatar from "./avatar/Avatar";

import AuthService from "../../service/auth/AuthService";

import AppConstants from "../../constants/AppConstants";

const Profile = (props) => {

    const [isProfileDisabled, setIsProfileDisabled] = useState(false);

    const history = useHistory()

    useEffect(() => {

        let username = props.match.params.username;

        const urlGetProfileByUsername = AppConstants.API_HOST + "/api/v1/profiles/" + username;
        const token = AuthService.getToken();

        fetch(urlGetProfileByUsername, {
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

            if (!data.isActive) {
                setIsProfileDisabled(true);
            }
        }).catch(() => {
            AuthService.logout();
            history.push("/");
        });
    });

    if (isProfileDisabled) {
        return (
            <div className="profile">
                <Header />
                <p className="disabled">Profile is disabled</p>
            </div>
        )
    } else {

        return (
        
            <div className="profile">
                <Header />
                <div className="profile-first-column">
                    <Menu />
                    <Avatar username={props.match.params.username} />
                    <PersonalInfo />
                </div>
                <div className="profile-second-column">
                </div>
            </div>

        );
    }
}

export default Profile;