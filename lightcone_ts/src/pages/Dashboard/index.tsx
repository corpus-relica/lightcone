import { useEffect, useState, useContext } from "react";

import { AuthContext } from '../../AuthContext';
import { getEvents, getPeople } from "../../api";
import EventsDash from "@/components/EventsDash";
import PeopleDash from "@/components/PeopleDash";

import { usePeopleStore } from "@/store/people";
import { useEventsStore } from "@/store/events";

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

import { Button } from "@/components/ui/button";

const DashboardPage = () => {
  const { people,
          // loading,
          fetchPeople,
          createPerson,
          updatePerson,
          destroyPerson } = usePeopleStore();
  const { events,
          //loading,
          fetchEvents, 
          createEvent,
          updateEvent,
          destroyEvent} = useEventsStore();

  const [selectedEventUID, setSelectedEventUID] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState(null);

  const [foobarbaz, setFoobarbaz] = useState(null);
  const { logout} = useContext(AuthContext);

  useEffect(() => {
    fetchEvents();
    // getEvents().then((data) => {
    //   setEvents(data.data);
    // }).catch((error) => {
    //    if (error.response && error.response.status === 419) {
    //      // Token has expired, log the user out
    //      logout();
    //    } else {
    //      // Handle other errors
    //    }
    // });

    fetchPeople();
  //   getPeople().then((data) => {
  //     setPeople(data.data.persons);
  //   }).catch((error) => {
  //      if (error.response && error.response.status === 419) {
  //        // Token has expired, log the user out
  //        logout();
  //      } else {
  //        // Handle other errors
  //      }
  //   });

  }, []);

  useEffect(() => {
    const uid = selectedEventUID ;
    if(uid === null){
      setSelectedEvent(null);
    }else{
      const event = uid && events.find((e:any) => e.uid === uid);
      setSelectedEvent(event);
    }
  } , [selectedEventUID]);

  const onPersonSubmit = async (personData: any) => {
    console.log("PERSON DATAS")
    console.log(personData)
    if(personData.uid === 0){
      await createPerson(personData);
    }else if(personData.uid > 0){
      await updatePerson(personData);
    }
    // fetchPeople();
  }

  const onPersonDelete = async (personData: any) => {
    console.log("PERSON TO DELETE DATAS")
    console.log(personData)
    await destroyPerson(personData);
    // fetchPeople();
  }

  const onEventSubmit = async (eventData: any) => {
    console.log("EVENT DATAS")
    console.log(eventData)
    if(eventData.uid === 0){
      await createEvent(eventData);
    }else if(eventData.uid > 0){
      await updateEvent(eventData);
    }
    // fetchEvents();
  }

  const onEventDelete = async (eventData: any) => {
    console.log("EVENT TO DELETE DATAS")
    console.log(eventData)
    // await destroyEvent(eventData);
    // fetchEvents();
  }

  return (
    <>
      foobarbaz
      <Button type="submit" onClick={logout}>
        logout
      </Button>
      <Tabs defaultValue="events" className="w-full">
        <TabsList>
          <TabsTrigger value="events">Events</TabsTrigger>
          <TabsTrigger value="people">People</TabsTrigger>
        </TabsList>
        <TabsContent value="events">
          <EventsDash events={events}
                      selectedEvent={selectedEvent}
                      people={people}
                      onSubmit={onEventSubmit}
                      onDelete={onEventDelete}
                      setSelectedEventUID={setSelectedEventUID}/>
        </TabsContent>
        <TabsContent value="people" className="w-full">
          <PeopleDash people={people}
                      onSubmit={onPersonSubmit}
                      onDelete={onPersonDelete}/>
        </TabsContent>
      </Tabs>
      </>
  )
}

export default DashboardPage;
