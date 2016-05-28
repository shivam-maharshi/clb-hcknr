INSERT INTO page
(page_id, page_namespace, page_title, page_restrictions, page_is_redirect, page_is_new, page_random, page_touched, page_links_updated, page_latest, page_len, page_content_model, page_lang)
VALUES
(111112, 0, 'Nimisha', '', 0, 1, 0.609159297879, '0000000000000000000000000000', null, 999998, 14, null, null);

INSERT INTO revision 
(rev_id, rev_page, rev_text_id, rev_comment, rev_user, rev_user_text, rev_timestamp, rev_minor_edit, rev_deleted, rev_len, rev_parent_id, rev_sha1, rev_content_model, rev_content_format) 
VALUES 
(999998, 111112, 999998, 'rev_comment', 0, 'rev_user_text', '3230313630353238323033303130', 0, 0, 14, 0, '347a67346c786667716d6c6b743477793370336a7472616d786d627663716a', null, null);

INSERT INTO text (old_id, old_text, old_flags) VALUES (999998, 'shivam text', 'utf-8');

# Select query

SELECT * FROM revision, page WHERE revision.rev_id = page.page_latest AND page.page_title = 'shivam' LIMIT 0 , 30;

SELECT * FROM text WHERE old_id =12088;

select * from page where page_title = 'Shivam';