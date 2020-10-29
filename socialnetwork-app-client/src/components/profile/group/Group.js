import React, { useEffect, useState } from "react";

import "./Group.css";

import GroupProfile from "./profile/GroupProfile";

import Header from "../../header/Header";

import AppConstants from "../../../constants/AppConstants";

import AuthService from "../../../service/auth/AuthService";

const Group = (props) => {

    const [group, setGroup] = useState({}); 
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, serError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        getGroupById();
    }, []);

    const getGroupById = () => {

        const groupId = props.match.params.id;

        const urlGetGroupById = AppConstants.API_HOST + "/group-service/groups/" + groupId;

        fetch(urlGetGroupById, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + AuthService.getToken(),
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw response;
            }

            return response.json();
        
        }).then(data => {
            setGroup(data);
            setIsLoaded(true);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                serError(true)
                setErrorMessage(body.status + " " + body.error);
                console.log("Status: " + body.status + " - Error: " + body.error + " - Message: " + body.message);
            })
        })
    }

    if (error) {
        return <div>Error. Message: {errorMessage}</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {
        
        return (
        
            <div>
                <Header />
                <div className="group-detail">
                    <div className="group-title">
                        <p>{group.groupName}</p>
                    </div>
                    <div className="group-avatar">
                        <img src={'data:image/jpeg;base64,' + group.groupAvatar} />
                    </div>
                    <div className="group-profiles border border-dark rounded">
                        {group.users.map(user => {
                            return (
                               <GroupProfile key={user.id} userId={user.id} />
                            )
                        })}
                    </div>
                </div>
            </div>
        )
    }
}

export default Group;