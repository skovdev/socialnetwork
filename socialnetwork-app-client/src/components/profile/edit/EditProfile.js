import React, { useState, useEffect } from "react";

import "./EditProfile.css";

import Header from "../../header/Header";

import AppConstants from "../../../constants/AppConstants";

import AuthService from "../../../service/auth/AuthService";

import { Modal } from "react-bootstrap";

import UpdateCurrentAvatar from "./avatar/UpdateCurrentAvatar";

const EditProfile = (props) => {

    const [profile, setProfile] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const [avatar, setAvatar] = useState('');

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [country, setCountry] = useState('');
    const [city, setCity] = useState('');
    const [address, setAddress] = useState('');
    const [phone, setPhone] = useState('');
    const [birthday, setBirthday] = useState('');
    const [familyStatus, setFamilyStatus] = useState('');

    const [showChangePasswordModalSuccess, setShowChangePasswordModalSuccess] = useState(false);

    const handleCloseChangePasswordModalSuccess = () => setShowChangePasswordModalSuccess(false);
    const handleShowChangePasswordModalSuccess = () => setShowChangePasswordModalSuccess(true);

    const [showChangePasswordModalFail, setShowChangePasswordModalFail] = useState(false);

    const handleCloseChangePasswordModalFail= () => setShowChangePasswordModalFail(false);
    const handleShowChangePasswordModalFail = () => setShowChangePasswordModalFail(true);


    useEffect(() => {
        loadProfileInformationByUsername();
    }, []);

    useEffect(() => {
        loadProfileAvatar();
    }, []);

    const loadProfileAvatar = () => {

        const urlGetProfileAvatar = AppConstants.API_HOST + "/api/v1/profiles/avatar";

        fetch(urlGetProfileAvatar, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed load avatar of profile");
            }

            return response.text();

        }).then(data => {
            setAvatar(data);
            setIsLoaded(true);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            setErrorMessage(error.message);
        });
    };

    const loadProfileInformationByUsername = () => {

        const urlGetProfileByUsername = AppConstants.API_HOST + "/api/v1/profiles/" + props.match.params.username + "/edit";

        fetch(urlGetProfileByUsername, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken(),
                "Content-Type" : "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed load information of profile");
            }
            
            return response.json();
        
        }).then(data => {
            setProfile(data);
            setIsLoaded(true);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            setErrorMessage(error.message);
        });
    };

    const updateProfileInformation = (event) => {

        event.preventDefault();

        const profileId = AuthService.getProfile().sub;

        const urlUpdateProfile = AppConstants.API_HOST + "/api/v1/profiles/" + profileId;

        let updatedProfile = {
            firstName: firstName ? firstName : profile.firstName,
            lastName: lastName ? lastName : profile.lastName,
            country: country ? country : profile.country,
            city: city ? city : profile.city,
            address: address ? address : profile.address,
            phone: phone ? phone : profile.address,
            birthday: birthday ? birthday : profile.birthday,
            familyStatus: familyStatus ? familyStatus : profile.familyStatus
        };

        fetch(urlUpdateProfile, {
            method: "PUT",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken(),
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(updatedProfile)
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed update information of profile");
            }

            return response.json();

        }).then(data => {
            setIsLoaded(true);
            alert("Updated");
            console.log(data);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            setErrorMessage(error.message);
            
        });
    };

    const defaultAvatar = () => {

        const urlSetDefaultAvatar = AppConstants.API_HOST + "/api/v1/profiles/avatar?username=" + AuthService.getProfile().username;

        fetch(urlSetDefaultAvatar, {
            method: "DELETE",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            }
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed set default avatar for profile");
            }

            return response.text();

        }).then(data => {
            setAvatar(data);
            setIsLoaded(true);
            console.log(data);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            setErrorMessage(error.message);
        });
    };

    const changePassword = () => {

        const urlChangePassword = AppConstants.API_HOST + "/api/v1/user/change-password";

        let data = {
            username: AuthService.getProfile().username,
            oldPassword: oldPassword,
            newPassword: newPassword
        }

        fetch(urlChangePassword, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + AuthService.getToken(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {

            if (!response.ok) {
                throw new Error("Failed change password of user");
            }

            return response.json();

        }).then(() => {
            handleCloseChangePasswordModalSuccess(false);
            handleShowChangePasswordModalSuccess(true);
            
        }).catch(() => {
            handleCloseChangePasswordModalFail(false);
            handleShowChangePasswordModalFail(true);            
        });
    };

    if (error) {

        return (

            <div>
                <p>Test</p>
            </div>
            
        );
    } else if (!isLoaded) {

        return (

            <div>
                <p>Loading...</p>
            </div>
            
        );
    } else {
        return (
            <div>
                <Header />
                <div className="change-password-modal-success">
                    <Modal show={showChangePasswordModalSuccess} onHide={handleCloseChangePasswordModalSuccess}>
                        <Modal.Header className="alert alert-success" closeButton>
                            <Modal.Title>Success</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <p>Password has created successfully</p>
                        </Modal.Body>
                    </Modal>
                </div>
                <div className="change-password-modal-fail">
                    <Modal show={showChangePasswordModalFail} onHide={handleCloseChangePasswordModalFail}>
                        <Modal.Header className="alert alert-danger" closeButton>
                            <Modal.Title>Fail</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <p>Error. You couldn't change password</p>
                        </Modal.Body>
                    </Modal>
                </div>
                <div className="edit-profile">
                    <h2>Profile Information</h2>
                    <div className="current-profile-avatar">
                        <img className="rounded border" src={'data:image/jpeg;base64,' + avatar} />
                        <div className="avatar-buttons-wrapper">
                            <UpdateCurrentAvatar setAvatar={setAvatar} />
                            <div className="mt-3">
                                <button className="btn btn-dark" onClick={defaultAvatar}>Set Default Avatar</button>
                            </div>
                        </div>
                    </div>
                    <h4>Change password</h4>
                    <div className="change-current-password">
                        <div className="form-group input-width">
                            <label htmlFor="oldPassword">Old password</label>
                            <input type="password" id="oldPassword" className="form-control" onChange={e => setOldPassword(e.target.value)} />
                        </div>
                        <div className="form-group input-width">
                            <label htmlFor="newPassword">New password</label>
                            <input type="password" id="newPassword" className="form-control" onChange={e => setNewPassword(e.target.value)} />
                        </div>
                        <button className="btn btn-dark mb-3" onClick={changePassword}>Change</button>     
                    </div>
                    <h4>Personal Information</h4>
                    <div className="form-group input-width">
                        <label htmlFor="firstName">First name</label>
                        <input type="text" id="firstName" className="form-control" onChange={e => setFirstName(e.target.value)} defaultValue={profile.firstName} />
                    </div>
                    <div className="form-group input-width">
                        <label htmlFor="lastName">Last name</label>
                        <input type="text" id="lastName" className="form-control" onChange={e => setLastName(e.target.value)} defaultValue={profile.lastName} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="country">Country</label>
                        <input type="text" id="country" className="form-control" onChange={e => setCountry(e.target.value)} defaultValue={profile.country} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="city">City</label>
                        <input type="text" id="city" className="form-control" onChange={e => setCity(e.target.value)} defaultValue={profile.city} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="address">Address</label>
                        <input type="text" id="address" className="form-control" onChange={e => setAddress(e.target.value)} defaultValue={profile.address} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="phone">Phone</label>
                        <input type="text" id="phone" className="form-control" onChange={e => setPhone(e.target.value)} defaultValue={profile.phone} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="birthday">Birthday</label>
                        <input type="text" id="birthday" className="form-control" onChange={e => setBirthday(e.target.value)} defaultValue={profile.birthday} />
                    </div>                    
                    <div className="form-group input-width">
                        <label htmlFor="familyStatus">Family status</label>
                        <input type="text" id="familyStatus" className="form-control" onChange={e => setFamilyStatus(e.target.value)} defaultValue={profile.familyStatus} />
                    </div>
                    <button className="btn btn-dark mb-3" onClick={updateProfileInformation}>Update</button>
                </div>
            </div>
        )
    }
}

export default EditProfile;