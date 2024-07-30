import React, { useState, useCallback } from 'react';
import { Box, TextField, Button, Modal, Typography } from '@mui/material';

import AuthService from '../../../../service/auth/AuthService';
import AppConstants from '../../../../constants/AppConstants';
import JwtTokenDecoder from '../../../../util/jwt/JwtTokenDecoder';

const ChangePasswordProfile = () => {
    const [newPassword, setNewPassword] = useState("");
    const [showModal, setShowModal] = useState({ success: false, fail: false });

    const handleModalClose = useCallback((type) => setShowModal((prev) => ({ ...prev, [type]: false })), []);
    const handleModalShow = useCallback((type) => setShowModal((prev) => ({ ...prev, [type]: true })), []);

    const handleChangePassword = useCallback(async () => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);

        const data = {
            username: decodedToken.username,
            newPassword
        };

        try {
            const response = await fetch(`${AppConstants.AUTH_SERVER_HOST}/api/v1/auth/change-password`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("An error occurred while changing password. Please try again later or contact support if the issue persists");
            }

            const result = await response.json();

            if (result) {
                handleModalShow('success');
            } else {
                handleModalShow('fail');
            }
        } catch (error) {
            console.log(error);
            handleModalShow('fail');
        }
    }, [newPassword, handleModalShow]);

    return (
        <Box>
            <Modal
                open={showModal.success}
                onClose={() => handleModalClose('success')}
            >
                <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', bgcolor: 'background.paper', p: 2, borderRadius: 1 }}>
                    <Typography variant="h6" color="success.main">Success</Typography>
                    <Typography>Password has been changed successfully</Typography>
                    <Button onClick={() => handleModalClose('success')}>Close</Button>
                </Box>
            </Modal>
            <Modal
                open={showModal.fail}
                onClose={() => handleModalClose('fail')}
            >
                <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', bgcolor: 'background.paper', p: 2, borderRadius: 1 }}>
                    <Typography variant="h6" color="error.main">Fail</Typography>
                    <Typography>Error. Could not change password</Typography>
                    <Button onClick={() => handleModalClose('fail')}>Close</Button>
                </Box>
            </Modal>
            <Box className="form-group input-width">
                <TextField
                    type="password"
                    id="newPassword"
                    label="New password"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    onChange={(e) => setNewPassword(e.target.value)}
                />
            </Box>
            <Button
                variant="contained"
                color="primary"
                fullWidth
                onClick={handleChangePassword}
                sx={{ backgroundColor: '#000', color: '#fff', '&:hover': { backgroundColor: '#333' } }}
            >
                Change
            </Button>
        </Box>
    );
};

export default ChangePasswordProfile;
