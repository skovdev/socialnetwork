import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import { Container, Box, Typography } from "@mui/material";

import "./AdminDashboard.css";

import AuthService from "../../../service/auth/AuthService";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";

import AdminHeader from "../header/AdminHeader";
import ProfileList from "./profile/ProfileList";

const AdminDashboard = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);

        if (!decodedToken.isAdmin) {
            navigate(`/profile/${decodedToken.username}`);
        }
    }, [navigate]);

    return (
        <Container>
            <AdminHeader />
            <Box className="dashboard">
                <Box className="monthly-registered-users">
                    <Typography variant="h5">Monthly Registered Users</Typography>
                </Box>
                <Box className="profile-panel">
                    <ProfileList />
                </Box>
            </Box>
        </Container>
    );
};

export default AdminDashboard;
