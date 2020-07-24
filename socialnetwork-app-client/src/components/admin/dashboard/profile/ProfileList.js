import React, { useState, useEffect } from "react";

import "./ProfileList.css";

import AppConstants from "../../../../constants/AppConstants";

import AuthService from "../../../../service/auth/AuthService";

const ProfileList = () => {

    const [profiles, setProfiles] = useState([])
    const [isLoaded, setIsLoaded] = useState(false)
    const [error, serError] = useState(false)

    useEffect(() => {
        loadUserProfiles();
    }, [])

    const loadUserProfiles = () => {

        const urlFindAllProfile = AppConstants.API_HOST + "/api/v1/profile"
        const token = AuthService.getToken();

        fetch(urlFindAllProfile, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.json()

        }).then(data => {
            setProfiles(data)
            setIsLoaded(true)
        }).catch(error => {
            serError(true)
            console.error(error)
        })
    }

    const changeStatus = (e, username) => {
        
        const status = e.target.value

        const token = AuthService.getToken()

        const urlChangeStatusProfile = AppConstants.API_HOST + `/api/v1/profile?username=${username}&isActive=${status}`

        fetch(urlChangeStatusProfile, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error()
            }

            return response.json()

        }).then(data => {
            console.log(data.message);
        }).catch(error => {
            console.error(error);
        })
    }

    if (error) {
        return <div>Error</div>
    } else if (!isLoaded) {
        return <div>Loading...</div>
    } else {

        return (

            <div className="profile-table">
                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Username</th>
                            <th scope="col">First Name</th>
                            <th scope="col">Last Name</th>
                            <th scope="col">Active Status</th>
                            <th scope="col">Profile Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {profiles.map((profile) => {
                            return (
                                <tr key={profile.id}>
                                    <th scope="row">{profile.id}</th>
                                    <th>{profile.customUser.username}</th>
                                    <th>{profile.customUser.firstName}</th>
                                    <th>{profile.customUser.lastName}</th>
                                    <th>{profile.isActive ? 
                                        <img className="status" src={process.env.PUBLIC_URL + "/images/profile/status/active.png"} /> 
                                        : 
                                        <img className="status" src={process.env.PUBLIC_URL + "/images/profile/status/inactive.png"} />}
                                    </th>
                                    <th>
                                        <select onChange={e => changeStatus(e, profile.customUser.username)} className="custom-select custom-select-sm">
                                            <option hidden>-- Select --</option>
                                            <option value="true">ACTIVE</option>
                                            <option value="false">INACTIVE</option>
                                        </select>
                                    </th>
                                </tr>
                            )
                        })}
                    </tbody>
                </table>
            </div>

        )
    }
}

export default ProfileList;