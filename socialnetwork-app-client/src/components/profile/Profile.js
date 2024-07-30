import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Container, Typography, createTheme, ThemeProvider } from "@mui/material";

import AuthService from "../../service/auth/AuthService";
import AppConstants from "../../constants/AppConstants";
import JwtTokenDecoder from "../../util/jwt/JwtTokenDecoder";

import Menu from "./menu/Menu";
import Avatar from "./avatar/Avatar";
import Header from "../header/Header";

import "./Profile.css";
import PersonalInfo from "./info/Personalnfo";

// Create a custom theme
const theme = createTheme({
    components: {
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '& .MuiOutlinedInput-notchedOutline': {
                        borderColor: '#000', // Default border color
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                        borderColor: '#000', // Hover border color
                    },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: '#000', // Focused border color
                    },
                },
            },
        },
        MuiInputLabel: {
            styleOverrides: {
                root: {
                    '&.Mui-focused': {
                        color: '#000', // Label color when focused
                    },
                },
            },
        },
    },
});

const Profile = () => {
    const [isProfileDisabled, setIsProfileDisabled] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const fetchProfile = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/users/${decodedToken.userId}`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("An error occurred while loading your profile. Please try again later or contact support if the issue persists");
            }

            const data = await response.json();

            if (!data.isActive) {
                setIsProfileDisabled(true);
            }
        } catch (error) {
            setError(error);
            AuthService.logout();
            navigate("/");
        }
    }, [navigate]);

    useEffect(() => {
        fetchProfile();
    }, [fetchProfile]);

    if (isProfileDisabled) {
        return (
            <ThemeProvider theme={theme}>
                <Container className="profile">
                    <Typography variant="h6" color="error" className="disabled">
                        Profile is disabled
                    </Typography>
                </Container>
            </ThemeProvider>
        );
    }

    if (error) {
        return (
            <ThemeProvider theme={theme}>
                <Container className="profile">
                    <Typography variant="h6" color="error" className="disabled">
                        {error.message}
                    </Typography>
                </Container>
            </ThemeProvider>
        );
    }

    return (
        <ThemeProvider theme={theme}>
            <Container className="profile">
                <Header />
                <Box className="profile-column">
                    <Menu />
                    <Avatar />
                    <PersonalInfo />
                </Box>
            </Container>
        </ThemeProvider>
    );
};

export default Profile;
