import React, { useState } from "react";

import { useNavigate } from "react-router-dom";

import { Button, TextField, Paper, Typography, Container, Snackbar } from '@mui/material';

import MuiAlert from '@mui/material/Alert';

import AuthService from '../../service/auth/AuthService';
import AppConstants from "../../constants/AppConstants";
import JwtTokenDecoder from "../../util/jwt/JwtTokenDecoder";

const SignIn = () => {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const [notification, setNotification] = useState({
        open: false,
        message: '',
        severity: 'success'
    });

    const navigate = useNavigate();

    const handleSubmit = async (event) => {

        event.preventDefault();

        const signInUrl = `${AppConstants.AUTH_SERVER_HOST}/api/v1/auth/sign-in`;
        
        try {

            let data = { username, password };

            const response = await fetch(signInUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('Login attempt unsuccessful. Please try again.');
            }

            const responseData = await response.json();

            AuthService.setToken(responseData.token);
            const decodedToken = JwtTokenDecoder.decode(responseData.token);

            navigate(decodedToken.isAdmin ? "/admin/dashboard" : "/profile/${decodedToken.username}");
            
            setNotification({
                open: true,
                message: 'Login successful!',
                severity: 'success'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: 'Authentication failed. Please check your username and password and try again.',
                severity: 'error'
            });
        }
    };

    const handleCloseSnackbar = () => {
        setNotification({ ...notification, open: false });
    };

    return (
        <Container component="main" maxWidth="xs">
            <Paper elevation={3} sx={{ p: 3}}>
                <Typography component="h1" variant="h5">Sign In</Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
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
                        value={username}
                        onChange={e => setUsername(e.target.value)} />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
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
                        value={password}
                        onChange={e => setPassword(e.target.value)} />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ backgroundColor: 'black', '&:hover': { backgroundColor: '#333' } }}
                        disabled={!username || !password}
                        style={{ marginTop: '20px' }}>
                        Sign In
                    </Button>
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

export default SignIn;
