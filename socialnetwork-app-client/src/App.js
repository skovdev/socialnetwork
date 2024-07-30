import React from "react";

import { BrowserRouter as Router, Routes, Route} from "react-router-dom";

import "./App.css";

import Main from "./components/main/Main";
import Profile from "./components/profile/Profile";
import EditProfile from "./components/profile/edit/EditProfile";
import AuthenticationRoute from "./components/route/auth/AuthenticationRoute";
import AdminDashboard from "./components/admin/dashboard/AdminDashboard";

const App = () => {
    return (
        <Router>
            <div className="app">
                <Routes>
                    <Route path="/" element={<Main />} />
                    <Route path="/profile/:username" element={<AuthenticationRoute component={<Profile />} />} />
                    <Route path="/profile/:username/edit" element={<AuthenticationRoute component={<EditProfile />} />} />
                    <Route path="/admin/dashboard" element={<AuthenticationRoute component={<AdminDashboard />} />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;