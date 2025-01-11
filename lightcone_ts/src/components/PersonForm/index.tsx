import React, { useState,useEffect } from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Separator } from "@/components/ui/separator";

const PersonForm = ({ onSubmit, initialData, onDelete}) => {
  const [uid, setUID] = useState(initialData?.id || 0);
  const [name, setName] = useState(initialData?.name || '');

  useEffect(() => {
    if (initialData) {
      setUID(initialData.id);
      setName(initialData.name);
    }else{
      setUID(0);
      setName('');
    }
  }, [initialData]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = {
      uid:uid,
      name,
    };
    onSubmit(formData);
  };

  const handleDelete = (e) => {
    e.preventDefault();
    onDelete(uid);
  }

  return (
    <div className="bg-card rounded-lg shadow-sm p-6">
      <div>
        prson uid: {uid}
      </div>
      <form onSubmit={handleSubmit} className="space-y-8">
        <div className="space-y-4">
          <h2 className="text-lg font-semibold">
            {initialData ? 'Edit Person' : 'Create New Person'}
          </h2>
          <Separator />
        </div>

        <div className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Person Name
            </label>
            <Input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Enter person name"
              className="w-full"
            />
          </div>
        </div>
        <div className="pt-4">
          {initialData ?
            <>
            <Button type='submit' className="w-full">
              Update Person
            </Button>
            <Button type='button' onClick={handleDelete} className="w-full bg-red-600">
              Delete Person
            </Button>
            </>
           :
            <>
            <Button type='submit' className="w-full">
              Create Person
            </Button>
            </>
          }
        </div>
      </form>
    </div>
  )
}

export default PersonForm;
