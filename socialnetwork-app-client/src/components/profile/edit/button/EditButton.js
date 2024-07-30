import React from "react";
import { Link } from "react-router-dom";
import { Button, styled } from "@mui/material";
import "./EditButton.css";

const StyledButton = styled(Button)({
    backgroundColor: '#000',
    color: '#fff',
    '&:hover': {
        backgroundColor: '#444',
    },
    width: '100%',
});

const EditButton = ({ username }) => {
    return (
        <div className="button-container">
            <StyledButton
                variant="contained"
                component={Link}
                to={`/profile/${username}/edit`}
                className="button-edit-profile"
            >
                Edit
            </StyledButton>
        </div>
    );
};

export default EditButton;
