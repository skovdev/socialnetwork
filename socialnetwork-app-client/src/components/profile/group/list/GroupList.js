import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Modal, InputGroup, FormControl, Button } from "react-bootstrap";

import './GroupList.css';

import Header from "../../../header/Header";

import AppConstants from "../../../../constants/AppConstants";

import AuthService from "../../../../service/auth/AuthService";

const GroupList = (props) => {

    const [groups, setGroups] = useState([]);

    const [groupName, setGroupName] = useState('');
    const [groupType, setGroupType] = useState('');

    const [showCreateGroupModal, setShowCreateGroupModal] = useState(false);

    const handleCloseCreateGroupModal = () => setShowCreateGroupModal(false);
    const handleShowCreateGroupModal = () => setShowCreateGroupModal(true);

    const [showCreateGroupModalSuccess, setShowCreateGroupModalSuccess] = useState(false);

    const handleCloseCreateGroupModalSuccess = () => setShowCreateGroupModalSuccess(false);
    const handleShowCreateGroupModalSuccess = () => setShowCreateGroupModalSuccess(true);

    const [showCreateGroupModalFail, setShowCreateGroupModalFail] = useState(false);

    const handleCloseCreateGroupModalFail= () => setShowCreateGroupModalFail(false);
    const handleShowCreateGroupModalFail = () => setShowCreateGroupModalFail(true);

    useEffect(() => {
        loadUserGroups();
    }, [])

    const loadUserGroups = () => {

        const token = AuthService.getToken();

        const username = props.match.params.username;

        const urlLoadUserGroups = AppConstants.API_HOST + "/group-service/groups?username=" + username;

        fetch(urlLoadUserGroups, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        }).then(response => {
            return response.json();
        }).then(data => {
            setGroups(data);
        });
    }

    const createGroup = (event) => {

        event.preventDefault();

        const token = AuthService.getToken();

        const urlCreateGroup = AppConstants.API_HOST + "/group-service/groups?username=" + AuthService.getProfile().username;

        const data = {
            groupName: groupName,
            groupType: groupType
        }

        fetch(urlCreateGroup, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {
            
            if (!response.ok) {
                throw new Error(response.status);
            }

            return response.json()

        }).then(data => {
            handleCloseCreateGroupModal(false);
            handleShowCreateGroupModalSuccess(true);
            console.log(data.message);
        }).catch(error => {
            handleCloseCreateGroupModal(false);
            handleShowCreateGroupModalFail(true);
            console.log(error.message);
        })
    }

    return (

        <div className="profile">
            <Header />
            <div className="create-group">
                <div className="create-group-modal">
                    <Modal show={showCreateGroupModal} onHide={handleCloseCreateGroupModal}>
                        <Modal.Header closeButton>
                            <Modal.Title>Create group</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <InputGroup className="mb-3">
                                <FormControl placeholder="Group name" value={groupName} onChange={e => setGroupName(e.target.value)} />
                            </InputGroup>
                            <InputGroup className="mb-3">
                                <FormControl as="select" onChange={e => setGroupType(e.target.value)}>
                                    <option hidden>-- Select type --</option>
                                    <option value="FAMILY">Family</option>
                                    <option value="CLUB">Club</option>
                                    <option value="BUY_AND_SELL">Buy and Sell</option>
                                    <option value="SOCIAL_LEARNING">Social learning</option>
                                    <option value="VIDEO_GAMES">Video games</option>
                                    <option value="TRAVEL">Travel</option>
                                    <option value="TEAM">Team</option>
                                    <option value="SUPPORT">Support</option>
                                </FormControl>
                            </InputGroup>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="dark" disabled={!groupName || !groupType} onClick={createGroup}>Create</Button>
                        </Modal.Footer>
                    </Modal>
                </div>
                <div className="create-group-modal-success">
                    <Modal show={showCreateGroupModalSuccess} onHide={handleCloseCreateGroupModalSuccess}>
                        <Modal.Header className="alert alert-success" closeButton>
                            <Modal.Title>Success</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <p>Group has created successfully</p>
                        </Modal.Body>
                    </Modal>
                </div>
                <div className="create-group-modal-fail">
                    <Modal show={showCreateGroupModalFail} onHide={handleCloseCreateGroupModalFail}>
                        <Modal.Header className="alert alert-danger" closeButton>
                            <Modal.Title>Fail</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <p>Group is exist</p>
                        </Modal.Body>
                    </Modal>
                </div>
            </div>
            <div className="group-list">
                <button className="btn btn-dark button-create-group" onClick={handleShowCreateGroupModal}>Create group</button>
                {groups.map(group => {
                    return (
                        <div className="group">
                            <img className="rounded" src={'data:image/jpeg;base64,' + group.avatar} />
                            <div className="group-info">
                                <p>Name:<Link className="group-link" to={"/group/" + group.id}>{group.groupName}</Link></p>
                                <span>Amount users: {group.groupAmountUsers}</span>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default GroupList;