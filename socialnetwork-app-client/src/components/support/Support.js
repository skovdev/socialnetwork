import React, { useEffect, useState } from "react";

import axios from "axios";

import AuthService from "../../service/auth/AuthService";

import AppConstants from "../../constants/AppConstants";

const Support = () => {

    const [subjects, setSubjects] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(false);

    useEffect(() => {

        const getSubjects = AppConstants.API_HOST + "/api/v1/supports";
        const token = AuthService.getToken();

        axios.get(getSubjects, {
            headers: {
                "Authorization": "Bearer " + token
            }
        }).then(response => {
            setSubjects(response);
            setIsLoaded(true);
        }).catch(error => {
            setIsLoaded(false);
            setError(true);
            console.log(error);
        });
    }, []);

   if (error) {
       return <div class="error">Error</div>
   } else if (!isLoaded) {
       return <div>Loading...</div>
   } else {
       return (
           <div class="support">
               {subjects.map(subject => {
                   return (<div class="subject">
                               <div>{subject.id}</div>
                               <div>{subject.title}</div>
                               <div>{subject.description}</div>
                           </div>
                   )
               })};
           </div>
       )
   }

}

export default Support;