import React, { useState, useEffect, useCallback } from "react";
import { Container, Box, Typography, TextField, Button, CircularProgress, Avatar } from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";

import AppConstants from "../../../constants/AppConstants";
import AuthService from "../../../service/auth/AuthService";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";

import Header from "../../header/Header";
import UpdateCurrentAvatar from "./avatar/UpdateCurrentAvatar";
import DefaultAvatar from "./avatar/DefaultAvatar";
import ChangePasswordProfile from "./password/ChangePasswordProfile";

import "./EditProfile.css";

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

const EditProfile = () => {
    const [profile, setProfile] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [avatar, setAvatar] = useState("");

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [country, setCountry] = useState("");
    const [city, setCity] = useState("");
    const [address, setAddress] = useState("");
    const [phone, setPhone] = useState("");
    const [birthDay, setBirthDay] = useState("");
    const [familyStatus, setFamilyStatus] = useState("");

    const loadProfileAvatar = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/avatar`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Failed to load the avatar. Please try again later or contact support if the issue persists");
            }

            const data = await response.text();
            setAvatar(data);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    const loadProfileInformation = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/users/${decodedToken.userId}/edit`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Failed to load the profile information. Please try again later or contact support if the issue persists.");
            }

            const data = await response.json();
            setProfile(data);
            setFirstName(data.firstName);
            setLastName(data.lastName);
            setCountry(data.country);
            setCity(data.city);
            setAddress(data.address);
            setPhone(data.phone);
            setBirthDay(data.birthDay);
            setFamilyStatus(data.familyStatus);
            setIsLoaded(true);
        } catch (error) {
            setError(error);
        }
    }, []);

    const updateProfileInformation = async (event) => {
        event.preventDefault();

        const updatedProfile = {
            firstName: firstName,
            lastName: lastName,
            country: country,
            city: city,
            address: address,
            phone: phone,
            birthDay: birthDay,
            familyStatus: familyStatus,
        };

        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/users/${decodedToken.userId}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(updatedProfile),
            });

            if (!response.ok) {
                throw new Error("Failed to update the profile information. Please try again later or contact support if the issue persists.");
            }

            setIsLoaded(true);
            alert("Profile updated successfully");
        } catch (error) {
            setError(error);
        }
    };

    useEffect(() => {
        loadProfileInformation();
        loadProfileAvatar();
    }, [loadProfileInformation, loadProfileAvatar]);

    if (error) {
        return <Typography color="error">{error.message}</Typography>;
    }

    if (!isLoaded) {
        return <Box display="flex" justifyContent="center" alignItems="center" height="100vh"><CircularProgress /></Box>;
    }

    return (
        <ThemeProvider theme={theme}>
            <Container>
                <Header />
                <Box className="edit-profile">
                    <Typography variant="h4" gutterBottom>
                        Profile Information
                    </Typography>
                    <Box className="current-profile-avatar" mb={4} display="flex" alignItems="center">
                        <Avatar
                            src={`data:image/jpeg;base64,${avatar}`}
                            alt="Avatar"
                            sx={{ width: 150, height: 150, marginRight: 2, borderRadius: 1, border: '2px solid black' }}
                        />
                        <Box className="avatar-buttons-wrapper" display="flex" flexDirection="column" ml={2}>
                            <UpdateCurrentAvatar setAvatar={setAvatar} />
                            <DefaultAvatar setAvatar={setAvatar} />
                        </Box>
                    </Box>
                    <Typography variant="h6" gutterBottom>
                        Change Password
                    </Typography>
                    <Box mb={4}>
                        <ChangePasswordProfile />
                    </Box>
                    <Typography variant="h6" gutterBottom>
                        Personal Information
                    </Typography>
                    <Box component="form" onSubmit={updateProfileInformation} noValidate>
                        <TextField
                            label="First name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                        />
                        <TextField
                            label="Last name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                        />
                        <TextField
                            label="Country"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={country}
                            onChange={(e) => setCountry(e.target.value)}
                        />
                        <TextField
                            label="City"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                        />
                        <TextField
                            label="Address"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={address}
                            onChange={(e) => setAddress(e.target.value)}
                        />
                        <TextField
                            label="Phone"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                        />
                        <TextField
                            label="Birthday"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={birthDay}
                            onChange={(e) => setBirthDay(e.target.value)}
                        />
                        <TextField
                            label="Family Status"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={familyStatus}
                            onChange={(e) => setFamilyStatus(e.target.value)}
                        />
                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            sx={{
                                backgroundColor: '#000',
                                color: '#fff',
                                '&:hover': {
                                    backgroundColor: '#333',
                                },
                            }}
                        >
                            Update
                        </Button>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
};

export default EditProfile;
