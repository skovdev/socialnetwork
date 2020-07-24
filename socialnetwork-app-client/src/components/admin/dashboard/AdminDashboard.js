import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";

import "./AdminDashboard.css";

import AuthService from "../../../service/auth/AuthService";

import DecodeJwtToken from "../../../util/jwt/DecodeJwtToken";

import AdminHeader from "../header/AdminHeader";
import ProfileList from "./profile/ProfileList";

const AdminDashboard = () => {
        
    const history = useHistory();

    useEffect(() => {
        
        const decodedToken = DecodeJwtToken.decode(AuthService.getToken());

        if (!decodedToken.isAdmin) {
            history.push("/profile/" + decodedToken.username)
        }
    }, [history]);

    return (

        <div>
            <AdminHeader />
            <div className="dashboard">
                <div className="monthly-registered-users">
                </div>
                <div className="profile-panel">
                    <ProfileList />
                </div>
            </div>
        </div>
        
    )

}

export default AdminDashboard;