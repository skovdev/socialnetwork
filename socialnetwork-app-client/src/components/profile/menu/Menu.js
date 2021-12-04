import React from "react";

import { Link } from "react-router-dom";

import "./Menu.css";

import AuthService from "../../../service/auth/AuthService";

const Menu = () => {

    return (

        <div className="menu">
            <ul>
                <li>
                    <Link to={"/profile/" + AuthService.getProfile().username}>Main page</Link>
                </li>
                <li>
                    <Link to={"/profile/support"}>Support</Link>
                </li>
            </ul>
        </div>

    );
}
 
export default Menu;