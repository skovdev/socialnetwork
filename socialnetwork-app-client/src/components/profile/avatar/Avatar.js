import React, { useState, useEffect, useCallback } from "react";
import { Box, CircularProgress, Typography, Avatar as MuiAvatar } from "@mui/material";

import AuthService from "../../../service/auth/AuthService";
import AppConstants from "../../../constants/AppConstants";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";

import EditButton from "../edit/button/EditButton";

import "./Avatar.css";

const Avatar = () => {
    const [username, setUsername] = useState("");
    const [avatar, setAvatar] = useState("");
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);

    const fetchAvatar = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            setUsername(decodedToken.username);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/avatar`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Failed to load the avatar. Please try again later or contact support if the issue persists.");
            }

            const data = await response.text();
            setAvatar(data);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    useEffect(() => {
        fetchAvatar();
    }, [fetchAvatar]);

    if (error) {
        return <Typography color="error">{error.message}</Typography>;
    }

    if (!isLoaded) {
        return <CircularProgress />;
    }

    return (
        <Box className="avatar-profile">
            <MuiAvatar
                src={`data:image/jpeg;base64,${avatar}`}
                alt="Avatar"
                className="avatar-image"
            />
            <EditButton username={username} />
        </Box>
    );
};

export default Avatar;
