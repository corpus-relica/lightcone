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
  const { people, /*loading,*/ fetchPeople, createPerson, updatePerson } = usePeopleStore();
  const { events, /*loading,*/ fetchEvents } = useEventsStore();

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
    const uid = selectedEvent && selectedEvent[0];
    const event = uid && events.find((e:any) => e.uid === uid);

    console.log("Selected Event: ", event);
    setFoobarbaz(event);
  }, [selectedEvent]);

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
                      foobarbaz={foobarbaz}
                      people={people}
                      setSelectedEvent={setSelectedEvent}/>
        </TabsContent>
        <TabsContent value="people" className="w-full">
          <PeopleDash people={people} onSubmit={onPersonSubmit}/>
        </TabsContent>
      </Tabs>
      </>
  )
}

export default DashboardPage;
