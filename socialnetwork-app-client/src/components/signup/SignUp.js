import React, { useState } from "react";

import { Button, TextField, Grid, Typography, Container, Snackbar, Paper } from '@mui/material';

import MuiAlert from '@mui/material/Alert';

import AppConstants from "../../constants/AppConstants";

const SignUp = () => {

    const [formData, setFormData] = useState({
        username: '',
        firstName: '',
        lastName: '',
        password: '',
        country: '',
        city: '',
        address: '',
        phone: '',
        birthDay: '',
        familyStatus: ''
    });

    const [notification, setNotification] = useState({
        open: false,
        message: '',
        severity: 'success'
    });

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCloseSnackbar = () => {
        setNotification({ ...notification, open: false });
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        const signUpUrl = `${AppConstants.AUTH_SERVER_HOST}/api/v1/auth/sign-up`;

        try {

            const response = await fetch(signUpUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error('User already exists in the social network');
            }

            setNotification({
                open: true,
                message: 'User signed up successfully',
                severity: 'success'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: error.message,
                severity: 'error'
            });
        }
    };

    return (
        <Container component="main" maxWidth="sm">
            <Paper elevation={3} sx={{ p: 3 }}>
                <Typography component="h1" variant="h5" sx={{ mb: 2 }}>Sign Up</Typography>
                <form onSubmit={handleSubmit}>
                    <Grid container spacing={2}>
                        {Object.keys(formData).map((key) => (
                            <Grid item xs={12} key={key}>
                                <TextField
                                    fullWidth
                                    label={key.charAt(0).toUpperCase() + key.slice(1)}
                                    name={key}
                                    value={formData[key]}
                                    onChange={handleInputChange}
                                    variant="outlined"
                                    sx={{
                                        '& label.Mui-focused': {
                                            color: 'black',
                                        },
                                        '& label': {
                                            color: 'black',
                                        },
                                        '& .MuiOutlinedInput-root': {
                                            '&:hover fieldset': {
                                                borderColor: 'black',
                                            },
                                            '&.Mui-focused fieldset': {
                                                borderColor: 'black',
                                            },
                                        }
                                    }}
                                />
                            </Grid>
                        ))}
                        <Grid item xs={12}>
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{ backgroundColor: 'black', '&:hover': { backgroundColor: '#333' } }}
                                disabled={Object.values(formData).some(value => value === '')}
                            >
                                Sign Up
                            </Button>
                        </Grid>
                    </Grid>
                </form>
            </Paper>
            <Snackbar open={notification.open} autoHideDuration={6000} onClose={handleCloseSnackbar}>
                <MuiAlert onClose={handleCloseSnackbar} severity={notification.severity} elevation={6} variant="filled">
                    {notification.message}
                </MuiAlert>
            </Snackbar>
        </Container>
    );
};

export default SignUp;
