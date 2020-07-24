import React, { useState, useEffect } from "react";

import "./EditProfile.css";

import Header from "../../header/Header";

import AppConstants from "../../../constants/AppConstants";

import AuthService from "../../../service/auth/AuthService";

const EditProfile = (props) => {

    const [profile, setProfile] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const [avatar, setAvatar] = useState('');

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [country, setCountry] = useState('');
    const [city, setCity] = useState('');
    const [address, setAddress] = useState('');
    const [phone, setPhone] = useState('');
    const [birthday, setBirthday] = useState('');
    const [familyStatus, setFamilyStatus] = useState('');

    useEffect(() => {
        loadProfileInformationByUsername();
    }, []);

    useEffect(() => {
        loadProfileAvatar();
    }, []);

    const loadProfileAvatar = () => {

        const urlGetProfileAvatar = AppConstants.API_HOST + "/api/v1/profile/avatar";

        fetch(urlGetProfileAvatar, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            }
        }).then(response => {

            if (!response.ok) {
                throw response;
            }

            return response.text();

        }).then(data => {
            setAvatar(data);
            setIsLoaded(true);
        }).catch(error => {
            
            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error + " " + body.message);
            });
        });
    };

    const loadProfileInformationByUsername = () => {

        const urlGetProfileByUsername = AppConstants.API_HOST + "/api/v1/profile/" + props.match.params.username + "/edit";

        fetch(urlGetProfileByUsername, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken(),
                "Content-Type" : "application/json"
            }
        }).then(response => {

            if (!response.ok) {
                throw response;
            }
            
            return response.json();
        
        }).then(data => {
            setProfile(data);
            setIsLoaded(true);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error + " " + body.message);
            });
        });
    };

    const updateProfileInformation = (event) => {

        event.preventDefault();

        const profileId = AuthService.getProfile().sub;

        const urlUpdateProfile = AppConstants.API_HOST + "/api/v1/profile/" + profileId;

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
                throw response;
            }

            return response.json();

        }).then(data => {
            setIsLoaded(true);
            alert("Updated");
            console.log(data);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error + " " + body.message);
            });
        });
    };

    const updateAvatar = (event) => {

        let reader = new FileReader();
        let file = event.target.files[0];

        reader.onloadend = () => {
            setAvatar(reader.result.split(',')[1]);
        }

        reader.readAsDataURL(file);

        let data = new FormData();

        data.append("profileAvatar", file);

        const urlUpdateAvatar = AppConstants.API_HOST + "/api/v1/profile/avatar";

        fetch(urlUpdateAvatar, {
            method: "POST",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            },
            body: data
        }).then(response => {

            if (!response.ok) {
                throw response;
            }

            return response.json();

        }).then(data => {
            setIsLoaded(true);
            console.log(data);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error + " " + body.message);
            });
        });
    }

    const defaultAvatar = () => {

        const urlSetDefaultAvatar = AppConstants.API_HOST + "/api/v1/profile/avatar?username=" + AuthService.getProfile().username;

        fetch(urlSetDefaultAvatar, {
            method: "DELETE",
            headers: {
                "Authorization" : "Bearer " + AuthService.getToken()
            }
        }).then(response => {

            if (!response.ok) {
                throw response;
            }

            return response.text();

        }).then(data => {
            setAvatar(data);
            setIsLoaded(true);
            console.log(data);
        }).catch(error => {

            error.json().then(body => {
                setIsLoaded(false);
                setError(true);
                setErrorMessage(body.status + " " + body.error + " " + body.message);
            });
        });
    }

    if (error) {

        return (

            <div>
                <p>Error. Message: {errorMessage}</p>
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
                <div className="edit-profile">
                    <h2>Profile Information</h2>
                    <div className="current-profile-avatar">
                        <img className="rounded border" src={'data:image/jpeg;base64,' + avatar} />
                        <div className="avatar-buttons-wrapper">
                            <div className="button-upload-avatar mt-3 mr-3">
                                <button className="btn btn-dark">Upload avatar</button>
                                <input type="file" onChange={updateAvatar} name="profileAvatar" />
                            </div>
                            <div className="mt-3">
                                <button className="btn btn-dark" onClick={defaultAvatar}>Set Default Avatar</button>
                            </div>
                        </div>
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