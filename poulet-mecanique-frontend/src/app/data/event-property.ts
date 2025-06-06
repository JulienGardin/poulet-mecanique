export class EventProperty {

    id: number;
    label: string;
    discordChannel: string;
    category: string;

    constructor(id: number, label: string, discordChannel: string, category: string) {
        this.id = id;
        this.label = label;
        this.discordChannel = discordChannel;
        this.category = category;
    }

}