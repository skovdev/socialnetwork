import React, { useEffect, useState, useCallback } from "react";
import { AppBar, Toolbar, Typography, Avatar, Box, Menu, MenuItem, CircularProgress } from "@mui/material";

import "./AdminHeader.css";

import AuthService from "../../../service/auth/AuthService";
import AppConstants from "../../../constants/AppConstants";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";

const AdminHeader = () => {
    const [profile, setProfile] = useState({});
    const [username, setUsername] = useState("");
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [anchorEl, setAnchorEl] = useState(null);

    const loadUserProfile = useCallback(async () => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);

        setUsername(decodedToken.username);

        try {
            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("An error occurred while loading profile. Please try again later or contact support if the issue persists.");
            }

            const data = await response.json();
            setProfile(data);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    useEffect(() => {
        loadUserProfile();
    }, [loadUserProfile]);

    const handleMenuOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    if (error) {
        return <Typography color="error">{error.message}</Typography>;
    }

    if (!isLoaded) {
        return <Box display="flex" justifyContent="center" alignItems="center" height="100vh"><CircularProgress color="inherit" /></Box>;
    }

    return (
        <AppBar position="static" className="admin-header">
            <Toolbar className="toolbar">
                <Typography variant="h6" className="title">
                    SocialNetwork Administration
                </Typography>
                <Box className="admin-profile" onClick={handleMenuOpen}>
                    <Avatar
                        src={`data:image/jpeg;base64,${profile.avatar}`}
                        alt="Profile Avatar"
                    />
                    <Typography variant="body1" className="username">
                        {username}
                    </Typography>
                </Box>
                <Menu
                    id="menu-appbar"
                    anchorEl={anchorEl}
                    anchorOrigin={{
                        vertical: 'top',
                        horizontal: 'right',
                    }}
                    keepMounted
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'right',
                    }}
                    open={Boolean(anchorEl)}
                    onClose={handleMenuClose}
                >
                    <MenuItem onClick={handleMenuClose} component="a" href={`/profile/${username}`} className="menu-item">Profile</MenuItem>
                    <MenuItem onClick={handleMenuClose} className="menu-item">Edit</MenuItem>
                    <MenuItem onClick={handleMenuClose} className="menu-item">Logout</MenuItem>
                </Menu>
            </Toolbar>
        </AppBar>
    );
};

export default AdminHeader;
