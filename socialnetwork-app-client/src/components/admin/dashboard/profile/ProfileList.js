import React, { useState, useEffect, useCallback } from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Select, MenuItem, Typography, Box, CircularProgress } from "@mui/material";

import "./ProfileList.css";

import AppConstants from "../../../../constants/AppConstants";
import AuthService from "../../../../service/auth/AuthService";
import JwtTokenDecoder from "../../../../util/jwt/JwtTokenDecoder";

const ProfileList = () => {
    const [profiles, setProfiles] = useState([]);
    const [username, setUsername] = useState("");
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);

    const loadUserProfiles = useCallback(async () => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);

        setUsername(decodedToken.username);

        try {
            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("An error occurred while loading profiles. Please try again later or contact support if the issue persists.");
            }

            const data = await response.json();
            setProfiles(data);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    const changeStatus = useCallback(async (e, profileId) => {
        const status = e.target.value;
        const token = AuthService.getToken();

        try {
            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${profileId}/status?isActive=${status}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`
                },
            });

            if (!response.ok) {
                throw new Error("An error occurred while changing status. Please try again later or contact support if the issue persists.");
            }

            // Update the status locally
            setProfiles((prevProfiles) =>
                prevProfiles.map((profile) =>
                    profile.id === profileId ? { ...profile, isActive: status === 'true' } : profile
                )
            );
        } catch (error) {
            setError(error);
        }
    }, []);

    useEffect(() => {
        loadUserProfiles();
    }, [loadUserProfiles]);

    if (error) {
        return <Typography color="error">{error.message}</Typography>;
    }

    if (!isLoaded) {
        return <Box display="flex" justifyContent="center" alignItems="center" height="100vh"><CircularProgress /></Box>;
    }

    return (
        <div className="profile-table">
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>#</TableCell>
                            <TableCell>Username</TableCell>
                            <TableCell>Active Status</TableCell>
                            <TableCell>Profile Status</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {profiles.map((profile) => (
                            <TableRow key={profile.id}>
                                <TableCell>{profile.id}</TableCell>
                                <TableCell>{username}</TableCell>
                                <TableCell>
                                    <img
                                        className="status"
                                        src={
                                            profile.isActive
                                                ? `${process.env.PUBLIC_URL}/images/profile/status/active.png`
                                                : `${process.env.PUBLIC_URL}/images/profile/status/inactive.png`
                                        }
                                        alt={profile.isActive ? "Active" : "Inactive"}
                                    />
                                </TableCell>
                                <TableCell>
                                    <Select
                                        value={profile.isActive ? 'true' : 'false'}
                                        onChange={(e) => changeStatus(e, profile.id)}
                                        displayEmpty
                                    >
                                        <MenuItem value="true">ACTIVE</MenuItem>
                                        <MenuItem value="false">INACTIVE</MenuItem>
                                    </Select>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
};

export default ProfileList;
