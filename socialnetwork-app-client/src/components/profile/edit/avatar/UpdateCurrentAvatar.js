import React, { useCallback } from "react";
import { Box, Button, Input } from "@mui/material";

import AppConstants from "../../../../constants/AppConstants";
import AuthService from "../../../../service/auth/AuthService";
import HttpMethodConstants from "../../../../constants/HttpMethodConstants";
import JwtTokenDecoder from "../../../../util/jwt/JwtTokenDecoder";

const UpdateCurrentAvatar = ({ setAvatar }) => {

    const handleOnChangeEvent = useCallback(async (event) => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);

        let reader = new FileReader();
        let file = event.target.files[0];

        reader.onloadend = () => {
            setAvatar(reader.result.split(',')[1]);
        };

        reader.readAsDataURL(file);

        let data = new FormData();
        data.append("profileAvatar", file);

        try {
            const response = await fetch(`${AppConstants.API_GATEWAY_HOST}/api/v1/profiles/${decodedToken.profileId}/avatar`, {
                method: HttpMethodConstants.PUT,
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                body: data
            });

            if (!response.ok) {
                throw new Error("An error occurred while updating current avatar. Please try again later or contact support if the issue persists");
            }

            const result = await response.json();
            console.log(result.message);
        } catch (error) {
            console.error(error);
        }
    }, [setAvatar]);

    return (
        <Box display="flex" alignItems="center" mt={3} mr={3}>
            <Button variant="contained" component="label" sx={{ backgroundColor: '#000', color: '#fff', '&:hover': { backgroundColor: '#333' } }}>
                Upload Avatar
                <Input type="file" onChange={handleOnChangeEvent} name="profileAvatar" sx={{ display: 'none' }} />
            </Button>
        </Box>
    );
};

export default UpdateCurrentAvatar;
