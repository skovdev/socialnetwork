import React, { useState } from "react";

import "./Header.css";

import { Modal, Button, InputGroup, FormControl } from "react-bootstrap";

import AuthService from "../../service/auth/AuthService";

import AppConstants from "../../constants/AppConstants";

const Header = () => {

    const [username, setUsername] = useState('')
    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [password, setPassword] = useState('')
    const [country, setCountry] = useState('')
    const [city, setCity] = useState('')
    const [address, setAddress] = useState('')
    const [phone, setPhone] = useState('')
    const [birthday, setBirthday] = useState('')
    const [familyStatus, setFamilyStatus] = useState('')

    const [showRegistrationModal, setShowRegistrationModal] = useState(false)

    const handleCloseRegistrationModal = () => setShowRegistrationModal(false)
    const handleShowRegistrationModal = () => setShowRegistrationModal(true)

    const [showRegistrationSuccessModal, setShowRegistrationSuccessModal] = useState(false)

    const handleCloseRegistrationSuccessModal = () => setShowRegistrationSuccessModal(false)

    const [showRegistrationFailModal, setShowRegistrationFailModal] = useState(false)

    const handleCloseRegistrationFailModal = () => setShowRegistrationFailModal(false)

    const registration = (event) => {

        event.preventDefault();

        const registrationUrl = AppConstants.API_HOST + "/registration";

        let data = {
            username: username,
            firstName: firstName,
            lastName: lastName,
            password: password,
            country: country,
            city: city,
            address: address,
            phone: phone,
            birthday: birthday,
            familyStatus: familyStatus
        }

        fetch(registrationUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {
            return response.json()
        }).then(registered => {

            if (registered) {
                setShowRegistrationModal(false)
                setShowRegistrationSuccessModal(true)
            } else {
                setShowRegistrationModal(false)
                setShowRegistrationFailModal(true)
            }
        })        
    }

    return (
        
        <div className="header">
            <p className="title">SocialNetwork</p>
            <div>
                <Modal show={showRegistrationModal} onHide={handleCloseRegistrationModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Registration</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="First Name" value={firstName} onChange={e => setFirstName(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Last Name" value={lastName} onChange={e => setLastName(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Country" value={country} onChange={e => setCountry(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="City" value={city} onChange={e => setCity(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Address" value={address} onChange={e => setAddress(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Phone" value={phone} onChange={e => setPhone(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Birthday" value={birthday} onChange={e => setBirthday(e.target.value)} />
                        </InputGroup>
                        <InputGroup className="mb-3">
                            <FormControl placeholder="Family Status" value={familyStatus} onChange={e => setFamilyStatus(e.target.value)} />
                        </InputGroup>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="dark" disabled={!username || !firstName || !lastName || !password || !country || !city || !address || !phone || !birthday || !familyStatus} onClick={registration}>Registration</Button>
                    </Modal.Footer>
                </Modal>
            </div>
            <div className="registration-modal-success">
                <Modal show={showRegistrationSuccessModal} onHide={handleCloseRegistrationSuccessModal} className="alert">
                     <Modal.Header className="alert-success" closeButton>
                        <Modal.Title>Success</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <p>User has registered successfully</p>
                    </Modal.Body>
                </Modal>
            </div>
            <div className="registration-modal-fail">
                <Modal show={showRegistrationFailModal} onHide={handleCloseRegistrationFailModal}>
                     <Modal.Header className="alert-danger" closeButton>
                        <Modal.Title>Fail</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <p>User is exist in the social network</p>
                    </Modal.Body>
                </Modal>
            </div>
            {!AuthService.getToken() ? (
                <div className="registration-button">
                    <button className="btn btn-dark" onClick={handleShowRegistrationModal}>Registration</button>
                </div>
            ) : ('')}
         </div>
        
    );
}
 
export default Header;