import React from "react";
import { Link } from "react-router-dom";

import "./EditButton.css";

const EditButton = (props) => {

    return (

        <div>
            <Link to={"/profile/" + props.username + "/edit"} className="btn btn-dark m-1 button-edit-profile">Edit</Link>
        </div>

    )
}

export default EditButton;