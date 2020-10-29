import React, { useState } from 'react';

import { Modal } from 'react-bootstrap';

import AuthService from '../../../../service/auth/AuthService';

import AppConstants from '../../../../constants/AppConstants';

const ChangePasswordProfile = () => {

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const [showChangePasswordModalSuccess, setShowChangePasswordModalSuccess] = useState(false);

    const handleCloseChangePasswordModalSuccess = () => setShowChangePasswordModalSuccess(false);
    const handleShowChangePasswordModalSuccess = () => setShowChangePasswordModalSuccess(true);

    const [showChangePasswordModalFail, setShowChangePasswordModalFail] = useState(false);

    const handleCloseChangePasswordModalFail= () => setShowChangePasswordModalFail(false);
    const handleShowChangePasswordModalFail = () => setShowChangePasswordModalFail(true);

    const handleChangePassword = () => {

        const changePasswordEndpoint = AppConstants.API_HOST + "/user/change-password";

        let data = {
            username: AuthService.getProfile().username,
            oldPassword: oldPassword,
            newPassword: newPassword
        }

        fetch(changePasswordEndpoint, {
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

    return (
        <div>
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
            <div className="form-group input-width">
                <label htmlFor="oldPassword">Old password</label>
                    <input type="password" id="oldPassword" className="form-control" onChange={e => setOldPassword(e.target.value)} />
            </div>
            <div className="form-group input-width">
                <label htmlFor="newPassword">New password</label>
                <input type="password" id="newPassword" className="form-control" onChange={e => setNewPassword(e.target.value)} />
            </div>
            <button className="btn btn-dark mb-3" onClick={handleChangePassword}>Change</button>     
        </div>
    )
}

export default ChangePasswordProfile;