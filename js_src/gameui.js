
import { Suit, Rank, CardModel, Card } from "./cards.js";

export const GameUI =
{
    BOARD_WIDTH: 700,
    BOARD_HEIGHT: 700,

    cards: [],

    initialize: function()
    {
        GameUI.onResize();

        let svgDefs =
            '<clipPath id="COVER-CP">\n' +
            '<polygon points="0,0 80,0 80,125 0,125" '+
                'transform="translate(' + (80 * 12) + ',' + (123 * 0) + ')"/>\n' +
            '</clipPath>\n' +
            '<g id="COVER-RASTER">\n' +
            '<use xlink:href="#texture" clip-path="url(#COVER-CP)" ' +
                'transform="translate('+ (-80 * 12 - 40) + ',' + (-123 * 0 - 62.5) + ')"/>\n' +
            '</g>\n';

        for (let s = 0; s < Suit.length; s ++)
        for (let r = 0; r < Rank.length; r ++)
        {
            const card = new Card(s, r);

            const cardName = card.getCardName();

            svgDefs +=
                '<clipPath id="' + cardName + '-CP">\n' +
                '<polygon points="0,0 80,0 80,125 0,125" '+
                    'transform="translate(' + (80 * r) + ',' + (123 * s) + ')"/>\n' +
                '</clipPath>\n' +
                '<g id="' + cardName + '-RASTER">\n' +
                '<use xlink:href="#texture" clip-path="url(#' + cardName + '-CP)"\n' +
                    'transform="translate('+ (-80 * r - 40) + ',' + (-123 * s - 62.5) + ')"/>\n' +
                '</g>\n' +
                '<g id="' + cardName + '">\n' +
                '<use id="' + cardName + '-FRONT" xlink:href="#' + cardName + '-RASTER"/>\n' +
                '<use id="' + cardName + '-COVER" xlink:href="#COVER-RASTER" visibility="hidden"/>\n' +
                '<rect id="' + cardName + '-RECT" x="-45" y="-67.5" width="90" height="133" rx="10" ry="10" ' +
                    'fill="transparent" stroke-width="3" stroke="#FFFF00" visibility="hidden"/>' +
                '</g>\n';

            GameUI.cards.push(card);
        }

        $("#svg-defs").html($("#svg-defs").html() + svgDefs);
		
        GameUI.repaint();
    },

    onResize: function()
    {
        const w = window.innerWidth
            || document.documentElement.clientWidth
            || document.body.clientWidth;

        const h = window.innerHeight
            || document.documentElement.clientHeight
            || document.body.clientHeight;

        const containerSize = Math.min(h, w) - 20;

        $('svg').css({'width': containerSize + "px",
                      'height': containerSize + "px"});
    },

    repaint: function()
    {
        let svgCanvas = "";

        for (let card of GameUI.cards.sort(function(a, b) { return a.z - b.z; }))
            svgCanvas += '<use id="' + card.getCardName() + '-ID" xlink:href="#' + card.getCardName() + '"/>\n';

        $("#svg-canvas").html(svgCanvas);

        GameUI.registerCardEvents();
    },

    registerCardEvents: function()
    {
        const f = function(event)
        {
            if (GameUI.onCardClick != null)
                GameUI.onCardClick(event.data.card)
        };

        const fIn = function(event)
        {
            if (GameUI.onCardMouseEnter != null)
                GameUI.onCardMouseEnter(event.data.card)
        };

        const fOut = function(event)
        {
            if (GameUI.onCardMouseLeave != null)
                GameUI.onCardMouseLeave(event.data.card)
        };

        for (let card of GameUI.cards)
        {
            const data = { card : card }

            $("#" + card.getCardName() + "-ID")
                .click(data, f)
                .mouseenter(data, fIn)
                .mouseleave(data, fOut);
        }
    }
};
