export class FeedProperty {

    id: number;
    label: string;
    discordChannel: string;
    url: string;
    icon: string;
    filter: string;

    constructor(id: number, label: string, discordChannel: string, url: string, icon: string, filter: string) {
        this.id = id;
        this.label = label;
        this.discordChannel = discordChannel;
        this.url = url;
        this.icon = icon;
        this.filter = filter;
    }

}