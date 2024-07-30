import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Box, List, ListItem, ListItemText, createTheme, ThemeProvider } from "@mui/material";
import AuthService from "../../../service/auth/AuthService";
import JwtTokenDecoder from "../../../util/jwt/JwtTokenDecoder";
import "./Menu.css";

const theme = createTheme({
    components: {
        MuiListItem: {
            styleOverrides: {
                root: {
                    display: 'inline-block',
                    padding: '6px',
                    backgroundColor: '#000',
                    border: '1px solid #a5a5a5',
                    borderRadius: '5px',
                    color: '#fff',
                    textAlign: 'center',
                    textDecoration: 'none',
                    '&:hover': {
                        backgroundColor: '#444',
                        textDecoration: 'none',
                        color: '#fff',
                    },
                },
            },
        },
    },
});

const Menu = () => {
    const [username, setUsername] = useState("");

    useEffect(() => {
        const token = AuthService.getToken();
        const decodedToken = JwtTokenDecoder.decode(token);
        setUsername(decodedToken.username);
    }, []);

    return (
        <ThemeProvider theme={theme}>
            <Box className="menu">
                <List>
                    <ListItem button component={Link} to={`/profile/${username}`}>
                        <ListItemText primary="Main page" />
                    </ListItem>
                </List>
            </Box>
        </ThemeProvider>
    );
};

export default Menu;
