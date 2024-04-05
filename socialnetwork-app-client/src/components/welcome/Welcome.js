import React from "react";

import { Typography, Box } from '@mui/material';

const Welcome = () => {
    
    return (
        <Box sx={{ padding: 2 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Welcome to the social network
            </Typography>
            <Typography variant="h6">
                Connect with friends and the world around you. Explore, share, and engage on the social network.
            </Typography>
        </Box>
    );
};

export default Welcome;
