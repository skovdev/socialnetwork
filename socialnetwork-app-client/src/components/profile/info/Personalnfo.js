import React, { useState, useEffect, useCallback } from "react";
import { Box, Typography, CircularProgress } from "@mui/material";

import AuthService from "../../../service/auth/AuthService";
import AppConstants from "../../../constants/AppConstants";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";

import "./PersonalInfo.css";

const PersonalInfo = () => {
    const [profileInfo, setProfileInfo] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);

    const loadProfileInfo = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/users/${decodedToken.userId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Failed to load the profile information. Please try again later or contact support if the issue persists.");
            }

            const data = await response.json();
            setProfileInfo(data);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    useEffect(() => {
        loadProfileInfo();
    }, [loadProfileInfo]);

    if (error) {
        return <Typography color="error">{error.message}</Typography>;
    }

    if (!isLoaded) {
        return <CircularProgress />;
    }

    return (
        <Box className="personal-info">
            <Typography className="full-name" variant="h4" gutterBottom>
                {profileInfo.firstName} {profileInfo.lastName}
            </Typography>
            <Box className="additional-info">
                <Typography variant="h6">
                    Birthday: {profileInfo.birthDay}
                </Typography>
                <Typography variant="h6">
                    Country: {profileInfo.country}
                </Typography>
                <Typography variant="h6">
                    City: {profileInfo.city}
                </Typography>
                <Typography variant="h6">
                    Family status: {profileInfo.familyStatus}
                </Typography>
                <Typography variant="h6">
                    Phone: {profileInfo.phone}
                </Typography>
                <Typography variant="h6">
                    Address: {profileInfo.address}
                </Typography>
            </Box>
        </Box>
    );
};

export default PersonalInfo;
