import React, { useEffect, useState } from "react";

import { useNavigate } from "react-router-dom";

import { Box, Typography, Link, Grid } from '@mui/material';

import Header from "../header/Header";
import Welcome from "../welcome/Welcome";
import SignUp from "../signup/SignUp";
import SignIn from "../signin/SignIn";

import AuthService from "../../service/auth/AuthService";

const Main = () => {

    const navigate = useNavigate();

    const [showSignUp, setShowSignUp] = useState(false);

    useEffect(() => {
        const token = AuthService.getToken();
        if (token) {
            const username = AuthService.getAuthData().username;
            navigate(`/profile/${username}`);
        }
    }, [navigate]);

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Header />
            <Grid container spacing={2} sx={{ mt: 2 }}>
                <Grid item xs={12} md={6}>
                    <Welcome />
                </Grid>
                <Grid item xs={12} md={6} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    {showSignUp ? (
                        <>
                           <SignUp />
                            <Typography variant="body2" sx={{ mt: 2, color: 'black' }}>
                                Already have an account?{" "}
                                <Link component="button" variant="body2" onClick={() => setShowSignUp(false)} sx={{ cursor: 'pointer', color: 'grey', '&:hover': { color: '#333' }, fontSize: '1rem' }}>
                                    Sign in here
                                </Link>
                            </Typography>
                        </>
                    ) : (
                        <>
                            <SignIn />
                            <Typography variant="body2" sx={{ mt: 2, color: 'black' }}>
                                Need to create an account?{" "}
                                <Link component="button" variant="body2" onClick={() => setShowSignUp(true)} sx={{ cursor: 'pointer', color: 'grey', '&:hover': { color: '#333' }, fontSize: '1rem' }}>
                                    Create an account
                                </Link>
                            </Typography>
                        </>
                    )}
                </Grid>
            </Grid>
        </Box>
    );
};

export default Main;
