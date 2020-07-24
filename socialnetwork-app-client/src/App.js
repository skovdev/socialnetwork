import React from "react";
import { BrowserRouter as Router, Switch, Route} from "react-router-dom";

import AuthenticationRoute from "./components/route/auth/AuthenticationRoute";

import Main from "./components/main/Main";
import Profile from "./components/profile/Profile";
import EditProfile from "./components/profile/edit/EditProfile";
import Group from "./components/profile/group/Group";
import GroupList from "./components/profile/group/list/GroupList";
import AdminDashboard from "./components/admin/dashboard/AdminDashboard";

import "./App.css";

const App = () => {

    return (

        <Router>
            <div className="app">
                <Switch>
                    <Route exact path="/" component={Main} />
                    <AuthenticationRoute path="/admin/dashboard" component={AdminDashboard} />
                    <AuthenticationRoute path="/profile/:username" exact component={Profile} />
                    <AuthenticationRoute path="/profile/:username/edit" component={EditProfile} />
                    <AuthenticationRoute path="/profile/:username/groups" component={GroupList} />
                    <AuthenticationRoute path="/group/:id" component={Group} />
                </Switch>
            </div>
        </Router>

    );
}

export default App;