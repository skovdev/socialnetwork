import React, { useCallback } from "react";
import { Box, Button } from "@mui/material";

import AppConstants from "../../../../constants/AppConstants";
import AuthService from "../../../../service/auth/AuthService";
import JwtTokenDecoder from "../../../../util/jwt/JwtTokenDecoder";

const DefaultAvatar = ({ setAvatar }) => {

    const setDefaultAvatar = useCallback(async () => {
        try {
            const token = AuthService.getToken();
            const decodedToken = JwtTokenDecoder.decode(token);

            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/avatar`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!response.ok) {
                throw new Error("An error occurred while setting default avatar. Please try again later or contact support if the issue persists");
            }

            const avatar = await response.text();
            setAvatar(avatar);
        } catch (error) {
            console.error(error);
        }
    }, [setAvatar]);

    return (
        <Box mt={3}>
            <Button
                variant="contained"
                onClick={setDefaultAvatar}
                sx={{
                    backgroundColor: '#000',
                    color: '#fff',
                    '&:hover': {
                        backgroundColor: '#333',
                    },
                }}
            >
                Set Default Avatar
            </Button>
        </Box>
    );
};

export default DefaultAvatar;
