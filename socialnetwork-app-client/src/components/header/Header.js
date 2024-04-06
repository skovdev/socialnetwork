import React from 'react';

import { AppBar, Toolbar, Typography } from '@mui/material';

const Header = () => {
    
    return (
        <AppBar position="static" sx={{ backgroundColor: 'black' }}>
            <Toolbar>
                <Typography variant="h3" noWrap>
                    SocialNetwork
                </Typography>
            </Toolbar>
        </AppBar>
    );
};

export default Header;